package com.brawkurly.moneytoring;

import com.brawkurly.moneytoring.domain.ChangePrice;
import com.brawkurly.moneytoring.domain.ConsumerPrice;
import com.brawkurly.moneytoring.domain.Item;
import com.brawkurly.moneytoring.domain.ResponseDto;
import com.brawkurly.moneytoring.repository.ChangePriceRepository;
import com.brawkurly.moneytoring.repository.ConsumerPriceRepository;
import com.brawkurly.moneytoring.repository.ItemRepository;
import com.brawkurly.moneytoring.service.ConsumerPriceJdbcService;
import com.brawkurly.moneytoring.service.OrderService;
import com.brawkurly.moneytoring.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@EnableScheduling
@SpringBootApplication
@RequiredArgsConstructor
public class MoneytoringApplication {

	private final ItemRepository itemRepository;

	private final OrderService orderService;

	private final ProductService productService;

	private final ChangePriceRepository changePriceRepository;

	private final ConsumerPriceJdbcService consumerPriceJdbcService;

	private final ConsumerPriceRepository consumerPriceRepository;

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

	/*
	* 0시 0분 0초에 DB에 넣기
	* */
	@Scheduled(cron = "0 0 0 * * ?", zone="Asia/Seoul")
	public void updateProductCurrentPrice() {
		// 각 상품별(id) 적정가 계산
		for (Long i = 1L; i <= 8L; i++) {
			Item findItem = itemRepository.findById(i).get();
			// 적정가 구하기
			ResponseDto item = new ResponseDto();
			item.setProductId(i);
			item.setSupplyPrice(findItem.getSupplyPrice());
			item.setFairPrice(findItem.getSupplyPrice());
			productService.findFairPrice(item);

			// MySQL에 해당 적정가 이상의 예약 가져오기
			List<ConsumerPrice> consumerPriceList = consumerPriceRepository
					.findConsumerPricesByItemAndPurchaseTimeIsNullAndPriceGreaterThanEqual(findItem, item.getFairPrice());
			for (ConsumerPrice consumerPrice : consumerPriceList) {
				consumerPrice.ChangePrice(item.getFairPrice());
			}
			consumerPriceRepository.saveAll(consumerPriceList);

			// MySQL에 저장
			findItem.changeCurrentAndFairPrice(item.getFairPrice());
			itemRepository.save(findItem);

			ChangePrice changePrice = new ChangePrice(findItem.getFairPrice(), new Date(), findItem);
			changePriceRepository.save(changePrice);

		}

		// 레디스에 있는 consumerPrice MySQL로 옮기기
		consumerPriceJdbcService.post();
	}
}
