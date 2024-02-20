package com.calebklc.orderservice.external.gateway;

import com.calebklc.orderservice.core.constant.BizError;
import com.calebklc.orderservice.core.exception.BizException;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.Unit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleMapsApiGateway {

    private final GeoApiContext geoApiContext;

    public DistanceMatrix getDistanceMatrix(String origins, String destinations) {
        var request = createDistanceMatrixApiRequest(origins, destinations);

        try {
            return request.await();
        } catch (ApiException | InterruptedException | IOException e) {
            log.error("Failed to get distance matrix", e);
            throw new BizException(BizError.GOOGLE_MAPS_API_FAILED);
        }
    }

    DistanceMatrixApiRequest createDistanceMatrixApiRequest(String origins, String destinations) {
        return DistanceMatrixApi.newRequest(geoApiContext)
                .origins(origins)
                .destinations(destinations)
                .units(Unit.METRIC);
    }
}
