package com.brawkurly.moneytoring;

import com.brawkurly.moneytoring.domain.Item;
import com.brawkurly.moneytoring.repository.ItemRepository;
import com.brawkurly.moneytoring.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@EnableScheduling
@SpringBootApplication
public class MoneytoringApplication {

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private OrderService orderService;

	public static void main(String[] args) {
		SpringApplication.run(MoneytoringApplication.class, args);
	}

	// 실시간으로 소비자 가격 데이터 넣어줌
	// 상품1~8 중 한가지 랜덤
	// 1/3확률로 예약, 2/3확률로 구매
	// 가격 예약은 현재판매가보다 작으면서 공급자가-1000보다 크도록
	// 1분마다 생성
	@Scheduled(fixedRate = 1000*60)
	public void insertConsumerPrice() {
		// 상품 1~8 랜덤 값 생성
		String productId = String.valueOf((int)((Math.random()*10000)%8+1));
		Item item = itemRepository.findById(Long.parseLong(productId)).get();

		// 1/3확률로 예약, 2/3확률로 구매
		int flag = (int)((Math.random()*10000)%3+1);
		if(flag==1){
			//예약
			int tmp = (item.getCurrentPrice()-(item.getSupplyPrice()-300))/100;
			int price = item.getCurrentPrice()-(int)((Math.random()*10000)%tmp+1)*100;
			orderService.addReserve(item.getName(), price);
		}else{
			//구매
			orderService.addBuy(item.getName(), item.getCurrentPrice());
		}
	}
}
