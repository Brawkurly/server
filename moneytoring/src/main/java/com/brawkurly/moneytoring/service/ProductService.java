package com.brawkurly.moneytoring.service;

import com.brawkurly.moneytoring.controller.ProductController;
import com.brawkurly.moneytoring.domain.ChangePrice;
import com.brawkurly.moneytoring.domain.Item;
import com.brawkurly.moneytoring.repository.ChangePriceRepository;
import com.brawkurly.moneytoring.repository.ItemRepository;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ItemRepository itemRepository;

    private final ChangePriceRepository changePriceRepository;

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


        /*상품별 마켓컬리 가격변동 컴포넌트*/
        PageRequest pageRequest = PageRequest.of(0, 49, Sort.by(Sort.Direction.DESC, "createAt"));
        List<ChangePrice> changePriceList = changePriceRepository.changePriceList(item.get(), pageRequest);
        for(int i=0; i<changePriceList.size(); i++){
            ChangePrice changePrice = changePriceList.get(i);
            System.out.println(changePrice.getId());
            ProductController.ChangePriceDto changePriceDto = new ProductController.ChangePriceDto(changePrice.getPrice(), changePrice.getCreateAt());
            responseDto.getChangePrice().add(changePriceDto);
        }

        return responseDto;
    }
}
