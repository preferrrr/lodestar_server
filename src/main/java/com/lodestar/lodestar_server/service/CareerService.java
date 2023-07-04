package com.lodestar.lodestar_server.service;

import com.lodestar.lodestar_server.dto.CareerDto;
import com.lodestar.lodestar_server.dto.CareerDtos;
import com.lodestar.lodestar_server.dto.GetCareerDto;
import com.lodestar.lodestar_server.dto.GetCareerDtos;
import com.lodestar.lodestar_server.entity.Career;
import com.lodestar.lodestar_server.entity.User;
import com.lodestar.lodestar_server.repository.CareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CareerService {

    private final CareerRepository careerRepository;

    public void saveCareer(User user, CareerDtos careerRequestDto) {

        List<Career> careerList = new ArrayList<>();

        for(CareerDto dto : careerRequestDto.getArr()) {
            Career career = new Career();
            career.setUser(user);
            career.setX(dto.getX());
            career.setY1(dto.getY().get(0));
            career.setY2(dto.getY().get(1));
            career.setRangeName(dto.getRangeName());

            careerList.add(career);
        }

        careerRepository.saveAll(careerList);

    }

    public GetCareerDtos getCareer(User user) {

        GetCareerDtos dtos = new GetCareerDtos();
        dtos.setArr(new ArrayList<>());

        List<Career> careers = careerRepository.findCareersByUser(user);

        for(Career career: careers) {
            GetCareerDto dto = new GetCareerDto();

            dto.setId(career.getId());
            dto.setX(career.getX());

            List<Long> ys = new ArrayList<>();
            ys.add(career.getY1());
            ys.add(career.getY2());
            dto.setY(ys);

            dto.setRangeName(career.getRangeName());
            dtos.getArr().add(dto);
        }

        return dtos;

    }


    public void modifyCareer(User user, CareerDtos careerDtos) {

        List<Career> careers = careerRepository.findCareersByUser(user); // 원래 저장되어 있던 것들

        List<String> rangeNames1 = new ArrayList<>();
        for(Career career : careers) {
            rangeNames1.add(career.getRangeName()); // 추가된 커리어를 저장햐기 위함
        }

        List<Career> addCareers = new ArrayList<>();



        for(CareerDto careerDto : careerDtos.getArr()) { //추가된 커리어를 저장하기 위함
            if(!rangeNames1.contains(careerDto.getRangeName())) {
                Career career = new Career();
                career.setX(careerDto.getX());
                career.setUser(user);
                career.setY1(careerDto.getY().get(0));
                career.setY2(careerDto.getY().get(1));
                career.setRangeName(careerDto.getRangeName());
                addCareers.add(career);
            }
        }

        List<String> rangeName2 = new ArrayList<>();

        for(CareerDto careerDto : careerDtos.getArr()) {
            rangeName2.add(careerDto.getRangeName());
        }
        List<Career> deleteCareers = new ArrayList<>();

        for(Career career : careers) {
            if (!rangeName2.contains(career.getRangeName()))
                deleteCareers.add(career);
        }

        careerRepository.saveAll(addCareers);
        careerRepository.deleteAll(deleteCareers);


    }
}
