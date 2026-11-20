//package ra.demo.security.filter;
//
//import io.jsonwebtoken.Claims;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.util.AntPathMatcher;
//import org.springframework.web.server.ServerWebExchange;
//import ra.demo.security.jwt.JWTProvider;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
//@Component
//public class AuthenticationFilter implements GlobalFilter, Ordered {
//    @Autowired
//    private JWTProvider jwtProvider;
//    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
//    private final List<String> openEndpoints = List.of(
//            "/api/v1/auth/login",
//            "/api/v1/auth/register",
//            "/api/v1/public/**"
//    );
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerWebExchange resetExchange = exchange.mutate()
//                .request(builder -> builder.headers(
//                        httpHeaders->{
//                            httpHeaders.remove("X-User-Username");
//                            httpHeaders.remove("X-User-Role");
//                        }
//                ))
//                .build();
//
//        ServerHttpRequest request = exchange.getRequest();
//        String path = request.getURI().getPath();
//
//        //1. Kiểm tra URL hiện tại có nằm trong danh sách white-list hay không
//        boolean isSecured = openEndpoints.stream().noneMatch(uri -> antPathMatcher.match(uri, path));
//        if(!isSecured) {
//            return chain.filter(exchange);
//        }
//
//        //2. Kiểm tra header authorization
//        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return onError(exchange, "Unauthorized: Invalid or expired token", HttpStatus.UNAUTHORIZED);
//        }
//        String token = authHeader.substring(7);
//
//        //3. Xác thực token
//        if(!jwtProvider.validateToken(token)) {
//            return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
//        }
//
//        //4. Lấy thông tin claims và mutate request truyền xuống microservice phía sau
//        Claims claims = jwtProvider.extractClaimsJws(token);
//        String username = claims.getSubject();
//        String roles = claims.get("role", String.class);
//
//        ServerWebExchange mutateExchange = exchange.mutate()
//                .request(builder -> builder
//                        .header("X-User-Username", username)
//                        .header("X-User-Role", roles)
//                        .build())
//                .build();
//
//        return chain.filter(mutateExchange);
//    }
//
//    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
//        exchange.getResponse().setStatusCode(httpStatus);
//        return exchange.getResponse().setComplete();
//    }
//
//    @Override
//    public int getOrder() {
//        return -1;
//    }
//}
