# 자바 예외 계층

![image](https://github.com/Jnghne/study-spring-db-1/assets/57797520/8ef5b4ae-ada8-4c83-be0c-91412aa18cf7)
출처 : https://learnjava.co.in/checked-vs-unchecked-exceptions/

- 최상위 예외 클래스는 Throwable이다
- Throwable의 자식으로는 개발자가 예외처리를 할 수 있는 Exception과 처리하면 안되는 Error가 있다

## 1. Error

- 메모리 부족이나 시스템 오류와 같이 애플리케이션에서 복구 불가능한 시스템 예외이다.
- 애플리케이션 개발자는 이 예외를 잡아서 처리하려고 하지 말아야한다. (개발 시 건들지 말것)
    - 참고로 Error 의 상위 계층인 Throwable로 예외처리를 하게 되면 하위 예외인 Error도 예외처리 되기 때문에, 애플리케이션 로직에서는 Throwable 예외도 잡지 말아야 한다 (Exception 하위로 잡아야 한다)

## 2. Exception

- 애플리케이션 로직에서 사용할 수 있는 실질적인 최상위 예외 클래스이다.

### 1. Checked Exception

- Checked Exception은 컴파일러가 컴파일 단계에서 체크하는 예외이다.
- Exception 클래스와 해당 클래스를 상속받는 예외 클래스는 일부 케이스를 제외하고는 모두 Checked Exception이다.
    - RuntimeException과 상속받은 예외는 UnChecked Exception
- 장점
    - 개발자가 실수로 예외를 누락하지 않도록 컴파일러를 통해 문제를 잡아주는 훌륭한 안전장치이다.
- 단점
    - 모든 체크 예외는 처리하지 못하는 예외나, 크게 신경쓰고 싶지 않은 예외까지도 반드시 잡거나 던지도록 처리해야하기 때문에 번거로운 일이 된다.
        - ex) DB 연결 오류에 대한 예외는 서비스 로직에서 처리할 수 없다. 하지만 체크 예외이기 때문에 처리해야한다.
    - 의존관계에 따른 단점이 존재한다.
- 예시

  아래와 같이 Exception을 상속받은 커스텀 예외 클래스를 생성했다.

  Exception은 Checked Exception이기 때문에 상속받은 해당 클래스도 Checked Exception이다.

    ```java
    // 커스텀 예외 클래스
    class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }
    ```

  Repository 특정 메서드에서 어떤 동작을 수행했을 때 MyCheckedException이 발생하고 해당 예외를 호출하는 곳으로 던진다고 가정했을 때, Service 로직에서 해당 메서드를 호출하는 코드를 작성하면 컴파일러 단계에서 Checked Exception 오류가 발생한다.

  그래서 Service단에서는 해당 예외를 try/catch 구문으로 잡아서 처리하던가 , 아니면 throw new MyCheckedException으로 해당 service를 호출하는 곳으로 던져야 한다.

    ```java
    class Repository {
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }
    
    class Service {
      Repository repository = new Repository();
    
      /**
       * 예외를 잡아서 처리하는 코드
       * - 메서드 내에서 try~catch를 통해서 예외를 처리한다.
       */
      public void callCatch() {
          try {
              repository.call();
          } catch (MyCheckedException e) {
              // 예외 처리 로직
              log.info("예외 처리, message={}", e.getMessage(), e);
          }
      }
    
      /**
       * 체크 예외를 밖으로 던지는 코드
       * - 체크 예외는 예외를 잡지 않고 밖으로 던지려면 throws 예외를 메서드에 필수로 선언해야 한다.
       * @throws MyCheckedException
       */
      public void callThrow() throws MyCheckedException {
          repository.call();
      }
    }
    ```


### 2. Unchecked Exception

- Unchecked Exception은 컴파일러가 예외를 체크하지 않고, 런타임 시점에 발생하는 예외이다.
- Unchecked Exception도 Checked Exception과 기본적으로 동일하지만, 예외를 잡아서 처리하거나 던지지 않아도 된다 (던지지 않으면 자동으로 호출한 곳으로 던진다)
    - ⭐ 즉, Check 예외나 Unchecked 예외나 모든 예외는 잡거나 던지거나 둘중의 하나의 동작을 하지만, 차이점은 컴파일러가 체크하느냐 마느냐 차이이다.
- Exception 하위 클래스 중 RuntimeException과 상속받은 예외는 UnChecked Exception이다.
    - RuntimeException과 하위 Unchecked Exception은 RuntimeException 이름을 따서 ‘런타임 예외’라고도 부른다
- Error 클래스와 상속 받는 하위 클래스도 Unchecked Exception이다.
- 장점
    - 메서드 내에서 신경쓰고 싶지 않은 예외 또는 처리할 수 없는 예외를 처리하지 않아도 된다.
    - 신경쓰고싶지 않은 예외의 불필요한 의존관계가 없어진다.
- 단점
    - 개발자가 실수로 예외를 누락할 수 있다.
        - Runtime 에러보다는 Compile 에러가 비용이 훨씬 적기 때문에 조심해야 함
- 예시

  Unchecked Exception의 상위 클래스인 RuntimeException을 상속받는 커스텀 예외 클래스를 생성한다.

    ```java
    class MyUncheckedException extends RuntimeException {
            public MyUncheckedException(String message) {
                super(message);
            }
        }
    ```

    - Repository에서 해당 예외를 상위로 던질 때 Unchecked Exception이기 때문에 throws 예외 선언을 하지 않는다.
    - Service에서 예외가 발생하는 메서드를 호출할 때,
        - Checked Exception과 같이 예외를 잡아서 처리할 수도 있고, 상위로 던질수도 있다.
        - 상위로 던질 때는 throws 예외 선언을 하지 않아도 된다.
            - 명시적으로 throws를 선언해도 되는데, 이 경우는 IDE를 통해 다른 개발자들에게 예외 발생 가능성을 알려주기 위한 용도로 사용된다.

    ```java
    /**
     * Unchecked 예외
     * - 예외를 잡거나, 던지지 않아도 된다.
     * - 예외를 잡지 않으면 자동으로 밖으로 던진다
     */
    class Service {
        Repository repository = new Repository();
    
        /**
         * 필요한 경우 예외를 잡아서 처리하면 된다.
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                log.info("예외 처리, message={}",e.getMessage(), e);
            }
        }
    
        /**
         * 예외를 처리하지 않으면, 예외 발생 시 자연스럽게 상위로 넘어간다.
         * Checked Exception과는 다르게 throws 예외 선언을 하지 않아도 된다.
         */
        public void callThrow() {
            repository.call();
        }
    
    }
    class Repository {
        public void call() {
            throw new MyUncheckedException("ex");
        }
    }
    ```


### 3. Checked Exception vs Unchecked Exception

- 모든 예외는 잡아서 처리하거나(try/catch), 호출한 곳으로 던지거나(throw) 둘 중 하나의 동작을 해야한다.
- Unchecked Exception도 Checked Exception 모두 기본적으로 동일하게 동작하는데
- 둘의 차이로는 Unchecked Exception은 예외를 잡아서 처리하거나 던지지 않아도 된다는 점이다.
    - 던지지 않으면 자동으로 호출한 곳으로 던진다
- ⭐ 즉, Check 예외나 Unchecked 예외나 모든 예외는 잡거나 던지거나 둘중의 하나의 동작을 하지만, 차이점은 컴파일러가 체크하느냐 마느냐 차이이다.

### 4. 예외를 전부 처리하지 않으면 어떻게 될까 ?

4.1. main() 메서드 직접 호출하는 경우

- 예외에 대한 스택 트레이스를 출력하고 프로그램이 종료된다.

4.2. 웹서비스인 경우

- 발생한 예외가 WAS까지 올라서
- WAS에서 예외에 대해 적절한 처리를 수행하고 프로그램을 계속 수행한다.
    1. WAS에서 예외를 처리할 예외 페이지 또는 예외 메시지를 리턴한다. (에러 메시지가 출력됨)
    2. 필터나 인터셉터에서 예외를 처리한 뒤 클라이언트에게 반환한다.

---

# 예외 활용

**체크 예외, 언체크(런타임) 예외를 각각 언제 사용해야 할까 ?**

⭐ 기본 원칙은 다음 2가지를 지키면 된다. ⭐ 

- 1. 기본적으로 언체크(런타임) 예외를 사용하자
    - 이 원칙은 다음과 같은 Checked Exception의 문제점 때문이다.

      1.1. 대부분의 예외는 복구 불가능한 예외이다.

        - 발생하는 대부분의 예외는 데이터베이스, 네트워크 통신과 같은 시스템 레벨의 예외이다.
        - 이런 시스템 레벨의 문제들은 애플리케이션 로직에서 예외 처리해서 해결할 수 없는 문제들이다 (ex) DB 서버가 다운 되거나, 네트워크 연결이 끊기는 등)
        - 따라서 이런 문제들은 일관성 있게 공통으로 처리해야 한다
            - 오류만 남기고, 개발자가 인지하게 한뒤에 오류를 빠르게 처리하게 해야 한다.
            - 예시

                ```java
                static class Controller {
                        Service service = new Service();
                        public void request() throws SQLException, ConnectException {
                            service.logic();
                        }
                    }
                    static class Service {
                        Repository repository = new Repository();
                        NetworkClient networkClient = new NetworkClient();
                        
                        public void logic() throws ConnectException, SQLException {
                            repository.call();
                            networkClient.call();
                        }
                    }
                
                    static class NetworkClient {
                        public void call() throws ConnectException {
                            throw new ConnectException("연결 실패");
                        }
                    }
                
                    static class Repository {
                        public void call() throws SQLException {
                            throw new SQLException("ex");
                        }
                    }
                ```


        1.2. 의존관계에 대한 문제가 생긴다. 
        
        - throws를 통해 예외를 선언하는 과정에서 해당 예외에 의존하게 된다.
        - 만약 SQLException 을 Service 로직에서 throws 하고 있으면, service 클래스는 JDBC 기술인 SQLException에 의존하게 된다.
            - 만약 향후에 JDBC가 아닌 다른 기술로 변경하게 된다면, SQLException에 의존하고 있는 모든 코드를 수정해야 한다. (ex) JPA와 관련된 Exception)
            - ⭐ 즉, 구현체에 직접적으로 의존하게 된다는 문제가 발생한다. (DIP/OCP 위반)
        - 여기서 잠깐 !! Exception으로 처리하게 되면 되지 않을까 ?
            - Exception은 최상위 예외 타입이기 때문에 모든 Checked Exception을 밖으로 던지게 된다.
            - 그럼 꼭 체크해야 하는 중요한 체크 예외가 잡히지 않는 문제가 발생한다.
            - Exception 자체를 던지는 것은 안티패턴이기 때문에 하지 말기
- 2. 체크 예외는 비즈니스 로직상 의도적으로 던질 때만 사용하자
    - 반드시 잡아서 처리해야 하는 예외의 경우 체크 예외를 사용한다.
        - ex) 계좌이체 실패 예외 / 결제 포인트 부족 예외 / 로그인 ID, PW 불일치 예외
    - 단, 100% 체크 예외로 만들어야 하는 것은 아니다.
        - 상황에 따라 런타임 예외로 해두고 문서화를 잘해둘 수 도 있지만
        - 다만 개발자가 실수로 예외를 놓치면 안되는 매우 중요한 비즈니스 로직의 경우 Checked Exception으로 만들어 두고 컴파일러를 통해 예외를 놓치지 않도록 한다.

**Uncheck 예외 활용 방법**

1. Checked 예외를 처리하는 커스텀 Unchecked 예외 클래스를 생성한다
    - SQLException ⇒ RuntimeSQLException
2. SQLException이 발생하는 곳에 RuntimeSQLException으로 전환해서 예외를 던지도록 한다.
    - 비즈니스 로직 또는 컨트롤러 단에서는 잡을 수 있으면 예외 처리를 하고 아니면 무시할 수 있다. (보통 던지고 공통 예외 처리 계층에서 처리한다)

      ⇒ 다른 계층에서 구체적인 예외 클래스에 대해 의존하지 않아도 된다

3. 발생하는 해당 RuntimeException 는 예외 공통 처리 계층에서 (ControllerAdvice, Filter, Interceptor, 서블릿 오류페이지) 에서 처리한다.
4. 단, RuntimeException은 놓칠 수 있기 때문에 문서화를 잘해야 한다
    - 또는 코드에 throws를 명시해둔다.
    - 또는 주석에 throws 런타임예외 와 같이 남겨서 인지할 수 있게 한다)

**예외 포함과 스택 트레이스**

⭐예외를 전환할 때는 **꼭 기존 예외를 포함**해야 한다.⭐

```java
class RuntimeSqlException extends RuntimeException {
    public RuntimeSqlException() {
    }

    public RuntimeSqlException(String message) {
        super(message);
    }

    public RuntimeSqlException(Throwable cause) {
        super(cause);
    }
}
```

체크 예외인 SQLException을 런타임 예외인 RuntimeSqlException으로 전환하려할 때, 예외를 넘겨주지 않는다면

**기존 SQLException에서 어떤 예외가 발생했는지 Stack trace를 확인할 수 없다.** ❌

⇒ 에러의 원인을 알 수 없다 !!

```java
class Repository {
    public void call() {
        try {
            runSQL();
        } catch (SQLException e) {
            throw new RuntimeSqlException(); // 기존 예외를 포함하지 않음 !!!
        }
    }
    public void runSQL() throws SQLException {
        throw new SQLException("ex");
    }
}
```

---

# 스프링 예외 추상화

스프링은 각 데이터 접근 기술 별로 상이한 수십가지 예외에 대해서 일관된 예외 추상화를 제공한다.

예를 들어 DB 키 중복 예외를 처리하려면 각 데이터 접근 기술마다 커스텀 예외 클래스를 생성해줘야 할 것이다.

(ex) JDBCDuplicateException, JpaDuplicateException 등)

Spring은 DataAccessException이라는 추상화된 예외 인터페이스를 제공한다 (RuntimeException을 상속받음)

DataAccessException은 Transient 예외와 NonTransient 예외 2가지로 구분한다.

- TransientDataAccessException 예외 : 재시도했을 때 성공할 가능성이 있는 예외 (ex) Lock, Timeout)
- NonTransientDataAccessException 예외 : 재시도해도 무조건 실패하는 예외(ex) SQL문법 오류, 데이터베이스 제약조건 위배 등)

## 스프링 예외 변환기

스프링은 자체 예외 변환기를 통해 SQLException의 ErrorCode에 맞는 적절한 스프링 데이터 접근 예외로 변환해준다.

DB 관련 기능에서 매번 DataAccessException 하위 예외 클래스로 변환해주려면 복잡한 작업이 필요할 것이다.

그러나 Spring은 아래와 같이 자동 변환 기능을 제공한다.

```java
String sql = "select bad grammer";

try {
    Connection con = dataSource.getConnection();
    PreparedStatement pstmt = con.prepareStatement(sql);
    pstmt.executeQuery();
} catch (SQLException e) {
    assertThat(e.getErrorCode()).isEqualTo(42122);

    SQLErrorCodeSQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    DataAccessException resultEx = exTranslator.translate("select", sql, e);
    log.info("resultEx", resultEx);
    assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);
}
```