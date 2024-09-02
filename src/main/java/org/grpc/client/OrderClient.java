package org.grpc.client;

import com.grpc.stub.OrderCount;
import com.grpc.stub.OrderDetails;
import com.grpc.stub.OrderId;
import com.grpc.stub.OrderServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Iterator;
import static java.lang.Thread.sleep;

public class OrderClient {

    private static final Logger logger = LogManager.getLogger(OrderClient.class);

    /*Synchronous Method-> Client wait for server to get the response */
    public void createOrderForGivenOrderId() {
        ManagedChannel channel
                = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();
        OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub
                = OrderServiceGrpc.newBlockingStub(channel);
        OrderDetails orderDetails
                = orderServiceBlockingStub.createOrderForGivenOrderId(OrderId.newBuilder().setOrderId("1001").build());
        logger.info("Created Order having id {} and name {}", orderDetails.getOrderId(), orderDetails.getName());
    }

    /* Server Side Streaming */
    public void serverStreaming() {
        ManagedChannel channel
                = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();
        OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub
                = OrderServiceGrpc.newBlockingStub(channel);
       Iterator<OrderDetails> orders
               = orderServiceBlockingStub.streamAllOrderForGivenCount(OrderCount.newBuilder().setCount(10).build());
       while(orders.hasNext()) {
           OrderDetails orderDetails = orders.next();
           logger.info("Order Details for Order id : {} and name: {}",
                   orderDetails.getOrderId(), orderDetails.getName());
       }
    }

    /* Client side streaming */
    public void clientSideStreaming() {
        ManagedChannel channel
                = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();
        OrderServiceGrpc.OrderServiceStub orderServiceStub
                = OrderServiceGrpc.newStub(channel);
        StreamObserver<OrderDetails> requestObserver = orderServiceStub.uploadOrderDetails(new StreamObserver<OrderCount>() {
            @Override
            public void onNext(OrderCount orderCount) {
                logger.info("number of order get uploaded {}", orderCount.getCount());
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("Error");
            }

            @Override
            public void onCompleted() {
                logger.info("Complete uploading of order to server");
            }
        });
        //Upload student details to server
        try{
            for(int i = 0; i < 10; i++) {
                logger.info("Create student details and upload");
                OrderDetails orderDetails
                        = OrderDetails.newBuilder().setName("Item-"+Integer.toString(i)).setOrderId(Integer.toString(i)).build();
                requestObserver.onNext(orderDetails);
                sleep(10000);
            }
        }catch (Exception exception) {

        }
        requestObserver.onCompleted();
        try{
            sleep(10000);
        }catch (InterruptedException interruptedException) {

        }
    }

    /*Bi-Direction Stream*/
    public void bistreaming() {
        ManagedChannel channel
                = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();
        OrderServiceGrpc.OrderServiceStub studentNonBlockingStub =
                OrderServiceGrpc.newStub(channel);


        StreamObserver<OrderDetails> requestObserver
                = studentNonBlockingStub.uploadAOrderDetailsAndReturnCount(new StreamObserver<OrderCount>() {
            @Override
            public void onNext(OrderCount orderCount) {
                logger.info("ResponseObserver order count {}", orderCount.getCount());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                logger.info("Finish bi-directional streaming");
            }
        });
        try{
            for(int i = 0; i < 10; i++) {
                logger.info("Create student details and upload");
                OrderDetails studentDetails
                        = OrderDetails.newBuilder().setName("Item-"+Integer.toString(i)).setOrderId(Integer.toString(i)).build();
                requestObserver.onNext(studentDetails);
                sleep(10000);
            }
        }catch (Exception exception) {

        }
        requestObserver.onCompleted();
        try {
            sleep(10000);
        }catch (InterruptedException interruptedException){}
    }


}
