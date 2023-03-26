package api;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.52.1)",
    comments = "Source: energy_drinks_grpc_api.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ServiceEnergyDrinksGrpc {

  private ServiceEnergyDrinksGrpc() {}

  public static final String SERVICE_NAME = "api.ServiceEnergyDrinks";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<EnergyDrinksGrpcApi.ShopsRequest,
      EnergyDrinksGrpcApi.ShopsResponse> getGetShopsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetShops",
      requestType = EnergyDrinksGrpcApi.ShopsRequest.class,
      responseType = EnergyDrinksGrpcApi.ShopsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<EnergyDrinksGrpcApi.ShopsRequest,
      EnergyDrinksGrpcApi.ShopsResponse> getGetShopsMethod() {
    io.grpc.MethodDescriptor<EnergyDrinksGrpcApi.ShopsRequest, EnergyDrinksGrpcApi.ShopsResponse> getGetShopsMethod;
    if ((getGetShopsMethod = ServiceEnergyDrinksGrpc.getGetShopsMethod) == null) {
      synchronized (ServiceEnergyDrinksGrpc.class) {
        if ((getGetShopsMethod = ServiceEnergyDrinksGrpc.getGetShopsMethod) == null) {
          ServiceEnergyDrinksGrpc.getGetShopsMethod = getGetShopsMethod =
              io.grpc.MethodDescriptor.<EnergyDrinksGrpcApi.ShopsRequest, EnergyDrinksGrpcApi.ShopsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetShops"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  EnergyDrinksGrpcApi.ShopsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  EnergyDrinksGrpcApi.ShopsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ServiceEnergyDrinksMethodDescriptorSupplier("GetShops"))
              .build();
        }
      }
    }
    return getGetShopsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<EnergyDrinksGrpcApi.BrandsRequest,
      EnergyDrinksGrpcApi.BrandsResponse> getGetBrandsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetBrands",
      requestType = EnergyDrinksGrpcApi.BrandsRequest.class,
      responseType = EnergyDrinksGrpcApi.BrandsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<EnergyDrinksGrpcApi.BrandsRequest,
      EnergyDrinksGrpcApi.BrandsResponse> getGetBrandsMethod() {
    io.grpc.MethodDescriptor<EnergyDrinksGrpcApi.BrandsRequest, EnergyDrinksGrpcApi.BrandsResponse> getGetBrandsMethod;
    if ((getGetBrandsMethod = ServiceEnergyDrinksGrpc.getGetBrandsMethod) == null) {
      synchronized (ServiceEnergyDrinksGrpc.class) {
        if ((getGetBrandsMethod = ServiceEnergyDrinksGrpc.getGetBrandsMethod) == null) {
          ServiceEnergyDrinksGrpc.getGetBrandsMethod = getGetBrandsMethod =
              io.grpc.MethodDescriptor.<EnergyDrinksGrpcApi.BrandsRequest, EnergyDrinksGrpcApi.BrandsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetBrands"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  EnergyDrinksGrpcApi.BrandsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  EnergyDrinksGrpcApi.BrandsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ServiceEnergyDrinksMethodDescriptorSupplier("GetBrands"))
              .build();
        }
      }
    }
    return getGetBrandsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ServiceEnergyDrinksStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ServiceEnergyDrinksStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ServiceEnergyDrinksStub>() {
        @Override
        public ServiceEnergyDrinksStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ServiceEnergyDrinksStub(channel, callOptions);
        }
      };
    return ServiceEnergyDrinksStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ServiceEnergyDrinksBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ServiceEnergyDrinksBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ServiceEnergyDrinksBlockingStub>() {
        @Override
        public ServiceEnergyDrinksBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ServiceEnergyDrinksBlockingStub(channel, callOptions);
        }
      };
    return ServiceEnergyDrinksBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ServiceEnergyDrinksFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ServiceEnergyDrinksFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ServiceEnergyDrinksFutureStub>() {
        @Override
        public ServiceEnergyDrinksFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ServiceEnergyDrinksFutureStub(channel, callOptions);
        }
      };
    return ServiceEnergyDrinksFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class ServiceEnergyDrinksImplBase implements io.grpc.BindableService {

    /**
     */
    public void getShops(EnergyDrinksGrpcApi.ShopsRequest request,
                         io.grpc.stub.StreamObserver<EnergyDrinksGrpcApi.ShopsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetShopsMethod(), responseObserver);
    }

    /**
     */
    public void getBrands(EnergyDrinksGrpcApi.BrandsRequest request,
                          io.grpc.stub.StreamObserver<EnergyDrinksGrpcApi.BrandsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetBrandsMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetShopsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                EnergyDrinksGrpcApi.ShopsRequest,
                EnergyDrinksGrpcApi.ShopsResponse>(
                  this, METHODID_GET_SHOPS)))
          .addMethod(
            getGetBrandsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                EnergyDrinksGrpcApi.BrandsRequest,
                EnergyDrinksGrpcApi.BrandsResponse>(
                  this, METHODID_GET_BRANDS)))
          .build();
    }
  }

  /**
   */
  public static final class ServiceEnergyDrinksStub extends io.grpc.stub.AbstractAsyncStub<ServiceEnergyDrinksStub> {
    private ServiceEnergyDrinksStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ServiceEnergyDrinksStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ServiceEnergyDrinksStub(channel, callOptions);
    }

    /**
     */
    public void getShops(EnergyDrinksGrpcApi.ShopsRequest request,
                         io.grpc.stub.StreamObserver<EnergyDrinksGrpcApi.ShopsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetShopsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getBrands(EnergyDrinksGrpcApi.BrandsRequest request,
                          io.grpc.stub.StreamObserver<EnergyDrinksGrpcApi.BrandsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetBrandsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ServiceEnergyDrinksBlockingStub extends io.grpc.stub.AbstractBlockingStub<ServiceEnergyDrinksBlockingStub> {
    private ServiceEnergyDrinksBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ServiceEnergyDrinksBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ServiceEnergyDrinksBlockingStub(channel, callOptions);
    }

    /**
     */
    public EnergyDrinksGrpcApi.ShopsResponse getShops(EnergyDrinksGrpcApi.ShopsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetShopsMethod(), getCallOptions(), request);
    }

    /**
     */
    public EnergyDrinksGrpcApi.BrandsResponse getBrands(EnergyDrinksGrpcApi.BrandsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetBrandsMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ServiceEnergyDrinksFutureStub extends io.grpc.stub.AbstractFutureStub<ServiceEnergyDrinksFutureStub> {
    private ServiceEnergyDrinksFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ServiceEnergyDrinksFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ServiceEnergyDrinksFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<EnergyDrinksGrpcApi.ShopsResponse> getShops(
        EnergyDrinksGrpcApi.ShopsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetShopsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<EnergyDrinksGrpcApi.BrandsResponse> getBrands(
        EnergyDrinksGrpcApi.BrandsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetBrandsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_SHOPS = 0;
  private static final int METHODID_GET_BRANDS = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ServiceEnergyDrinksImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ServiceEnergyDrinksImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_SHOPS:
          serviceImpl.getShops((EnergyDrinksGrpcApi.ShopsRequest) request,
              (io.grpc.stub.StreamObserver<EnergyDrinksGrpcApi.ShopsResponse>) responseObserver);
          break;
        case METHODID_GET_BRANDS:
          serviceImpl.getBrands((EnergyDrinksGrpcApi.BrandsRequest) request,
              (io.grpc.stub.StreamObserver<EnergyDrinksGrpcApi.BrandsResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ServiceEnergyDrinksBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ServiceEnergyDrinksBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return EnergyDrinksGrpcApi.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ServiceEnergyDrinks");
    }
  }

  private static final class ServiceEnergyDrinksFileDescriptorSupplier
      extends ServiceEnergyDrinksBaseDescriptorSupplier {
    ServiceEnergyDrinksFileDescriptorSupplier() {}
  }

  private static final class ServiceEnergyDrinksMethodDescriptorSupplier
      extends ServiceEnergyDrinksBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ServiceEnergyDrinksMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ServiceEnergyDrinksGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ServiceEnergyDrinksFileDescriptorSupplier())
              .addMethod(getGetShopsMethod())
              .addMethod(getGetBrandsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
