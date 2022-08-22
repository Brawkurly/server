package com.brawkurly.moneytoring.service;

import com.brawkurly.moneytoring.controller.ProductController;
import com.brawkurly.moneytoring.domain.*;
import com.brawkurly.moneytoring.repository.ChangePriceRepository;
import com.brawkurly.moneytoring.repository.ConsumerPriceRepository;
import com.brawkurly.moneytoring.repository.ItemRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ItemRepository itemRepository;

    private final ChangePriceRepository changePriceRepository;

    private final RedisTemplate redisTemplate;

    private final ConsumerPriceRepository consumerPriceRepository;

    public ProductController.ResponseDto getProductData(Long id){
        ProductController.ResponseDto responseDto = new ProductController.ResponseDto();

        /*적정 가격 컴포넌트*/
        Optional<Item> item = itemRepository.findById(id);
        if(!item.isPresent()){
            throw new NoSuchElementException();
        }
        responseDto.setProductId(item.get().getId());
        responseDto.setProductName(item.get().getName());
        responseDto.setCurrentPrice(item.get().getCurrentPrice());
        responseDto.setSupplyPrice(item.get().getSupplyPrice());
        responseDto.setFairPrice(item.get().getFairPrice());

        /*상품별 소비자 예약 현황 컴포넌트*/

       responseDto.setConsumerRecentReserve(findRecentReserve(id));

        /*상품별 마켓컬리 가격변동 컴포넌트*/
        PageRequest pageRequest = PageRequest.of(0, 49, Sort.by(Sort.Direction.DESC, "createAt"));
        List<ChangePrice> changePriceList = changePriceRepository.changePriceList(item.get(), pageRequest);
        for(int i=0; i<changePriceList.size(); i++){
            ChangePrice changePrice = changePriceList.get(i);
            System.out.println(changePrice.getId());
            ProductController.ChangePriceDto changePriceDto = new ProductController.ChangePriceDto(changePrice.getPrice(), changePrice.getCreateAt());
            responseDto.getChangePrice().add(changePriceDto);
        }

        /*해당 상품의 가격대별 예약 수*/
        responseDto.setConsumerReserveCnt(findReserveCnt(id));

        /*실시간 인기 구매 상품 현황
         * key : consumer_price(테이블 명):status(상태-purchase/reserve):productId(상품아이디)
         * */
        consumerPopularity("purchase", responseDto);


        /*실시간 인기 예약 상품 현황
         * key : consumer_price(테이블 명):status(상태-reserve):productId(상품아이디)
         * */
        consumerPopularity("reserve", responseDto);


        /*오늘 판매 현황 컴포넌트
         * key : consumer_price(테이블 명):status(상태-reserve):productId(상품아이디)
         * */
        todayData(responseDto, item.get());

        return responseDto;
    }

    public void consumerPopularity(String status, ProductController.ResponseDto responseDto) {
        for (Long i = 1L; i <= 8L; i++) {
            Item tmpItem = itemRepository.findById(i).get();

            String key = "consumer_price:" + status + ":" + i;
            Long cnt = redisTemplate.opsForList().size(key);

            ProductController.ConsumerPopularityDto consumerPopularityDto = new ProductController.ConsumerPopularityDto(tmpItem.getName(), cnt);

            if (status.equals("purchase")) responseDto.getConsumerPopularityPurchase().add(consumerPopularityDto);
            else responseDto.getConsumerPopularityReserve().add(consumerPopularityDto);
        }

        if (status.equals("purchase"))
            Collections.sort(responseDto.getConsumerPopularityPurchase(), (o1, o2) -> (int) (o2.getCnt() - o1.getCnt()));
        else
            Collections.sort(responseDto.getConsumerPopularityReserve(), (o1, o2) -> (int) (o2.getCnt() - o1.getCnt()));
    }
    /*
     * 최근 해당 상품을 소비자가 예약 현황 상위 20개
     * */
    public List<ConsumerRecentReserveDto> findRecentReserve(Long id) {
        List<ConsumerRecentReserveDto> result = new ArrayList<>();
        ListOperations<String, ConsumerPriceDto> listOperations = redisTemplate.opsForList();
        String key = "consumer_price:reserve:"+id;
        long size = listOperations.size(key);
        long needSize = 20 - size;
        // 레디스에 들어있는 데이터가 20이하 -> mysql에서 부족한 수만큼 들고와야함
        if (needSize > 0) {
            List<ConsumerPriceDto> consumerPriceDtoList = listOperations.range(key, 0, size);
            List<ConsumerPriceDto> mappedList = objectMapper().convertValue(consumerPriceDtoList, new TypeReference<List<ConsumerPriceDto>>() {});
            for (int i = 0; i < size; i++) {
                result.add(new ConsumerRecentReserveDto(mappedList.get(i).getPrice(), mappedList.get(i).getReservationTime()));
            }
            Optional<Item> item = itemRepository.findById(id);
            List<ConsumerPrice> consumerPriceList = consumerPriceRepository.findTop20ByItemOrderByReservationTimeDesc(item.get());
            if(consumerPriceList.size() > 0) {
                for (int i = 0; i < needSize; i++) {
                    if (consumerPriceList.get(i) == null) {
                        break;
                    }
                    result.add(new ConsumerRecentReserveDto(consumerPriceList.get(i).getPrice(), consumerPriceList.get(i).getReservationTime()));
                }
            }
        } else {
            List<ConsumerPriceDto> consumerPriceDtoList = listOperations.range(key, 0, 20);
            for (ConsumerPriceDto consumerPriceDto: consumerPriceDtoList) {
                result.add(new ConsumerRecentReserveDto(consumerPriceDto.getPrice(), consumerPriceDto.getReservationTime()));
            }
        }
        return result;
    }

    /*
     * 해당 상품을 가격대별 예약 수
     * */
    public List<Map<String, Long>> findReserveCnt(Long id){
        Map<Long, Long> allPriceMap = new ConcurrentHashMap<>();
        // 레디스에서 데이터 가져오기
        ListOperations<String, ConsumerPriceDto> listOperations = redisTemplate.opsForList();
        String key = "consumer_price:reserve:"+id;
        List<ConsumerPriceDto> consumerPriceDtoList = listOperations.range(key, 0, -1);
        List<ConsumerPriceDto> mappedList = objectMapper().convertValue(consumerPriceDtoList, new TypeReference<List<ConsumerPriceDto>>() {});
        for (ConsumerPriceDto consumerPrice : mappedList) {
            Long price = Long.valueOf(consumerPrice.getPrice());
            if (allPriceMap.get(price) == null) {
                allPriceMap.put(price, 1L);
            } else {
                allPriceMap.put(price, allPriceMap.get(price)+1);
            }
        }

        // mysql에서 데이터 가져오기
        Optional<Item> item = itemRepository.findById(id);
        List<Map<String, Long>> rdbCntList = consumerPriceRepository.findGroupByReserveCnt(item.get());
        for (Map<String, Long> rdbCnt: rdbCntList){
            Long price = Long.valueOf(String.valueOf(rdbCnt.get("price")));
            if (allPriceMap.get(price) == null) {
                allPriceMap.put(price, Long.valueOf(rdbCnt.get("cnt")));
            } else {
                allPriceMap.put(price, allPriceMap.get(price) + Long.valueOf(rdbCnt.get("cnt")));
            }
        }
        List<Map<String, Long>> result = new ArrayList<>();
        Object[] keyArray = allPriceMap.keySet().toArray();
        Arrays.sort(keyArray);
        for (Object price : keyArray) {
            Map<String, Long> tmp = new ConcurrentHashMap<>();
            tmp.put("price", (Long) price);
            tmp.put("cnt", allPriceMap.get(price));
            result.add(tmp);
        }
        return result;
    }

    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // timestamp 형식 안따르도록 설정
        mapper.registerModules(new JavaTimeModule(), new Jdk8Module()); // LocalDateTime 매핑을 위해 모듈 활성화
        return mapper;
    }

    public void todayData(ProductController.ResponseDto responseDto, Item item){

        /*해당 상품 판매 데이터*/
        String purchaseKey = "consumer_price:" + "purchase:" + responseDto.getProductId();
        ListOperations<String, ConsumerPriceDto> purchaseListOperations = redisTemplate.opsForList();
        List<ConsumerPriceDto> purchaseConsumerPriceDtoList = purchaseListOperations.range(purchaseKey, 0, -1);
        List<ConsumerPriceDto> purchaseMappedList = objectMapper().convertValue(purchaseConsumerPriceDtoList, new TypeReference<List<ConsumerPriceDto>>() {});

        /*오늘 하루 해당 상품의 총 판매 금액입니다.*/
        Long sum = 0L;
        for(int i=0; i<purchaseMappedList.size(); i++){
            System.out.println(purchaseMappedList.get(i).getProductName() + " " + purchaseMappedList.get(i).getPrice());
            sum += purchaseMappedList.get(i).getPrice();
        }
        responseDto.setTotalPrice(sum);

        /*오늘 하루 해당 상품의 총 판매 수입니다.*/
        responseDto.setTotalSalesCnt(purchaseListOperations.size(purchaseKey));

        /*전일 대비 판매율입니다. ((오늘-어제)/어제)*100 */
        LocalDateTime startDatetime = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(0,0,0)); //어제 00:00:00
        LocalDateTime endDatetime = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(23,59,59)); //어제 23:59:59
        Long yesterdaySum = consumerPriceRepository.findAllByPurchaseTimeBetween(item, startDatetime, endDatetime);
        if(yesterdaySum==null) yesterdaySum = 0L;
        float per = (((float) sum-(float)yesterdaySum))/(float)yesterdaySum*100;
        responseDto.setDayToDay(per);

        /*해당 상품 예약 데이터*/
        String reserveKey = "consumer_price:" + "reserve:" + responseDto.getProductId();
        ListOperations<String, ConsumerPriceDto> reserveListOperations = redisTemplate.opsForList();
        List<ConsumerPriceDto> reserveConsumerPriceDtoList = reserveListOperations.range(reserveKey, 0, -1);

        /*오늘 하루 해당 상품의 총 예약 수입니다.*/
        responseDto.setConsumerReservationCnt(reserveListOperations.size(reserveKey));
    }

}
