package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ItemUpdateTest {

    @Autowired EntityManager em;

    @Test
    public void updateTest() throws Exception {
        // Given — 테스트 안에서 데이터를 직접 만듦
        Book book = new Book();
        book.setName("원래이름");
        book.setPrice(10000);
        em.persist(book);
        em.flush();

        // When — 값만 바꾸고 save()는 호출하지 않음
        Book findBook = em.find(Book.class, book.getId());
        findBook.setName("바뀐이름");

        // Then — 커밋 시점에 UPDATE 쿼리가 나가는 걸 로그로 확인
        em.flush();
    }
}
