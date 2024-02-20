package com.calebklc.orderservice.external.service;

import com.calebklc.orderservice.core.constant.BizError;
import com.calebklc.orderservice.core.exception.BizException;
import com.calebklc.orderservice.external.gateway.GoogleMapsApiGateway;
import com.google.maps.model.DistanceMatrixElementStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleMapsDistanceMatrixService implements DistanceMatrixService {

    private final GoogleMapsApiGateway googleMapsApiGateway;

    @Override
    public long getDistance(String origins, String destinations) {
        var distanceMatrix = googleMapsApiGateway.getDistanceMatrix(origins, destinations);

        var row = Arrays.stream(distanceMatrix.rows)
                .findFirst()
                .orElseThrow(() -> new BizException(BizError.DISTANCE_MATRIX_FAILED));

        var element = Arrays.stream(row.elements)
                .findFirst()
                .orElseThrow(() -> new BizException(BizError.DISTANCE_MATRIX_FAILED));

        if (!DistanceMatrixElementStatus.OK.equals(element.status)) {
            log.warn("Distance matrix failed, status: {}", element.status);
            throw new BizException(BizError.DISTANCE_MATRIX_FAILED);
        }

        return element.distance.inMeters;
    }
}
