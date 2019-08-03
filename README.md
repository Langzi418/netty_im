Netty的NioEventLoop线程中的循环可以分为以下几个步骤：

1. 轮询注册在selector上的IO事件（select）
2. 处理IO事件（processSelectedKey，unsafe.read() 开启事件传播）
3. 执行异步task (runAllTasks)

通常有两类NioEventLoop线程：boss和worker。

对于boss线程来说，轮询出来的基本都是 accept 事件，表示有新的连接，而worker线程轮询出来的基本都是read/write事件，表示网络的读写事件。

服务端在用户进程（main线程）中启动，并将处理新连接的过程封装成一个channel，对应的pipeline为：

`HeadContext -> ServerBootStrapAcceptor -> TailContext  `

## 新连接的建立

### 检测到有新连接接入

```java
private void processSelectedKey(SelectionKey k, AbstractNioChannel ch) {
    final AbstractNioChannel.NioUnsafe unsafe = ch.unsafe();

    int readyOps = k.readyOps();

    if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 
        	|| readyOps == 0) {
        //NioServerSocketChannel对应NioMessageUnsafe, NioSocketChannel对应NioByteUnsafe
        unsafe.read();
    }
}
```

下面是NioMessageUnsafe.read()。

```java
private final List<Object> readBuf = new ArrayList<Object>();

public void read(){
    final ChannelPipeline pipeline = pipeline();

    // 给每条连接创建NioSocketChannel,感兴趣的事件是read
    doReadMessages(readBuf);

    for (int i = 0; i < size; i ++) {
        pipeline.fireChannelRead(readBuf.get(i));
    }	
}
```

下面是**pipeline.fireChannelRead**的调用过程。

```java
// DefaultChannelPipeline.java
public final ChannelPipeline fireChannelRead(Object msg) {
    AbstractChannelHandlerContext.invokeChannelRead(head, msg);
    return this;
}

// AbstractChannelHandlerContext.java
static void invokeChannelRead(final AbstractChannelHandlerContext next, Object msg) { 
    // 是当前channel的事件循环线程
    if (executor.inEventLoop()) {
        next.invokeChannelRead(m);
    } else {
        executor.execute(()->{
            next.invokeChannelRead(m);
        });
    }
}

private void invokeChannelRead(Object msg) { 
    // 在服务端通过HeadContext -> ServerBootstrapAcceptor
    ((ChannelInboundHandler) handler()).channelRead(this, msg);
}

//ServerBootstrapAcceptor.java
public void channelRead(ChannelHandlerContext ctx, Object msg) {
    final Channel child = (Channel) msg;
    // 将用户代码中服务端定义的childHandler添加到NioSocketChannel,通常为ChannelInitializer
    child.pipeline().addLast(childHandler);
    
    childGroup.register(child)...
}
```

`addLast()`在添加完成后，会回调相应handler的`handlerAdded(DefaultChannelHandlerContext)`。其中，ChannelInitializer对应的handlerAdded会调用抽象方法initChannel()，这个方法就是我们实现来添加自定义handler的地方。(并且ChannelInitizer后在添加完成后删除自己)

这样之后，NioSocketChannel的pipeline对应为：HeadContext -> 用户自定义ChannelHandler -> TailContext。

### 将新的连接注册到NioEventLoop线程

接着，ServerBoostrapAcceptor中channelRead调用到register(child)。这个方法最终从workerGroup中循环选择一个NioEventLoop线程，然后将child注册到对应的selector上。若当前线程不是child对应的NioEventLoop线程，则启动NioEventLoop线程。若是，则直接执行注册逻辑。

```java
// AbstractChannel.java
public final void register(EventLoop eventLoop, final ChannelPromise promise) {
    if (eventLoop.inEventLoop()) {
        register0(promise);
    }else{
        eventLoop.execute(()->{register0(promise);})
    }
}

private void register0(ChannelPromise promise) {
    doRegister();

    if (isActive()) {
        if (firstRegistration) {
            pipeline.fireChannelActive();
        } else if (config().isAutoRead()) {
            beginRead();
        }
    }
}

beginRead(){
    doBeginRead();
}

//AbstractNioChannel.java
protected void doBeginRead() throws Exception {
    // Channel.read() or ChannelHandlerContext.read() was called
    final SelectionKey selectionKey = this.selectionKey;

    final int interestOps = selectionKey.interestOps();
    if ((interestOps & readInterestOp) == 0) {
        selectionKey.interestOps(interestOps | readInterestOp);
    }
}
```

这里其实就是将 `SelectionKey.OP_READ`事件注册到selector中去，表示这条通道已经可以开始处理read事件了。
