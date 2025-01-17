package net.thenextlvl.character.plugin.network;

import io.netty.channel.AbstractChannel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;

import java.net.SocketAddress;

class EmptyChannel extends AbstractChannel {
    private final ChannelConfig config = new DefaultChannelConfig(this);

    public EmptyChannel() {
        super(null);
    }

    @Override
    public ChannelConfig config() {
        config.setAutoRead(true);
        return config;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public ChannelMetadata metadata() {
        return new ChannelMetadata(true);
    }

    @Override
    protected AbstractUnsafe newUnsafe() {
        return null;
    }

    @Override
    protected boolean isCompatible(EventLoop eventLoop) {
        return false;
    }

    @Override
    protected SocketAddress localAddress0() {
        return null;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return null;
    }

    @Override
    protected void doBind(SocketAddress socketAddress) {
    }

    @Override
    protected void doDisconnect() {
    }

    @Override
    protected void doClose() {
    }

    @Override
    protected void doBeginRead() {
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer channelOutboundBuffer) {
    }
}
