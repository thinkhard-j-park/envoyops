# envoyops

Envoy [xDS protocol](https://www.envoyproxy.io/docs/envoy/latest/api-docs/xds_protocol) 의 구현체인 [java-control-plane](https://github.com/envoyproxy/java-control-plane) 을 사용하여 Envoy 리소스 설정을 동적으로 변경할 수 있게 한다.

- xDS 설정은 redis pub/sub을 통해 Control Plane 으로 전파된다.
- 전파된 설정은 xDS API (gRPC Stream) 을 통해 각 Control Plane에 연결된 envoy 인스턴스로 전달된다.

![envoy-ops](./img/enovy-ops.png)