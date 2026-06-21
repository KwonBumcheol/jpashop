# jpashop

Spring Boot + JPA 기반 쇼핑몰 실습 프로젝트

---

## 1단계 — 프로젝트 초기 설정

**프로젝트 생성 → View 환경 설정 → JPA + DB 설정**

Thymeleaf(뷰 템플릿)와 H2 인메모리 DB를 연결했습니다.

---

## 2단계 — 도메인 설계 (엔티티 클래스 개발)

**커밋: 10~엔티티 설계시 주의사항**

| 엔티티 | 역할 |
|--------|------|
| `Member` | 회원. `Address`를 `@Embedded`로 포함, `orders` 리스트는 `mappedBy`로 읽기 전용 |
| `Order` | 주문. `CascadeType.ALL`로 `OrderItem`, `Delivery`를 함께 저장/삭제 |
| `OrderItem` | 주문상품. 주문 가격(`orderPrice`)과 수량(`count`) 보관 |
| `Item` (abstract) | 상품 공통. `SINGLE_TABLE` 전략으로 `Book`, `Album`, `Movie`가 한 테이블에 저장 |
| `Delivery` | 배송. `Order`와 1:1 관계 |
| `Category` | 카테고리. 자기 자신과 부모/자식 관계(`@ManyToOne parent`, `@OneToMany child`) |
| `Address` | 값 타입(`@Embeddable`). 불변 객체로 설계(기본 생성자 `protected`) |

**주요 설계 포인트:**
- 모든 연관관계는 `fetch = LAZY`로 설정 → N+1 문제 방지
- `Order`, `Category`에 **연관관계 편의 메서드** 추가 (양방향 동기화)

---

## 3단계 — 회원 도메인 개발

**커밋: 16(리포지토리) → 17(서비스) → 18(테스트)**

### `MemberRepository` (`repository/MemberRepository.java`)

`EntityManager`를 직접 주입받아 CRUD 구현:
- `save()` → `em.persist()`
- `findOne()` → `em.find()`
- `findAll()` → JPQL
- `findByName()` → JPQL + 파라미터 바인딩

### `MemberService` (`service/MemberService.java`)

- 클래스 레벨: `@Transactional(readOnly = true)` → 조회 최적화
- `join()`: `readOnly=false` 트랜잭션으로 **중복 회원 검증 후 저장**
- `@RequiredArgsConstructor` + `final` 필드 → 생성자 주입

### `MemberServiceTest` (테스트)

- `회원가입()`: `em.flush()`로 쿼리를 실제 DB에 날려 검증
- `중복_회원_예외()`: `assertThrows`로 `IllegalStateException` 발생 확인

---

## 4단계 — 상품 도메인 개발

**커밋: 19(엔티티 비즈니스 로직) → 20(리포지토리) → 21(서비스)**

### `Item` 엔티티 비즈니스 로직 (`domain/item/Item.java`)

재고 관리 로직을 **엔티티 안에** 직접 구현 (도메인 모델 패턴):

```java
addStock(int quantity)    // 재고 증가
removeStock(int quantity) // 재고 감소 (0 미만이면 NotEnoughStockException)
```

### `NotEnoughStockException`

`RuntimeException`을 상속한 커스텀 예외. 재고 부족 시 발생.

### `ItemRepository` (`repository/ItemRepository.java`)

`save()`가 `MemberRepository`와 다른 점:

```java
if (item.getId() == null) em.persist(item); // 신규
else em.merge(item);                         // 기존 수정
```

### `ItemService` (`service/ItemService.java`)

`MemberService`와 동일한 패턴. `@Transactional(readOnly = true)` 기본, `saveItem()`만 쓰기 트랜잭션.

---

## 현재까지 구현된 전체 구조

```
도메인 설계
  └─ Member / Order / OrderItem / Item(Book·Album·Movie) / Delivery / Category / Address

회원 도메인
  └─ MemberRepository → MemberService → MemberServiceTest

상품 도메인
  └─ Item 비즈니스 로직 → ItemRepository → ItemService
```

**다음 단계:** 주문 도메인 개발 (OrderRepository, OrderService)
`Order` 엔티티와 연관관계 메서드는 이미 설계되어 있으므로 서비스 로직만 추가하면 됩니다.
