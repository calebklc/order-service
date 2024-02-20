package com.calebklc.orderservice.external.service;

import com.calebklc.orderservice.TestConstant;
import com.calebklc.orderservice.core.exception.BizException;
import com.calebklc.orderservice.external.gateway.GoogleMapsApiGateway;
import com.google.maps.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoogleMapsDistanceMatrixServiceTest {

    @Mock
    private GoogleMapsApiGateway googleMapsApiGateway;

    @InjectMocks
    private GoogleMapsDistanceMatrixService googleMapsDistanceMatrixService;

    private String origins;
    private String destinations;
    private long distanceInMeters;

    @BeforeEach
    void setUp() {
        origins = String.join(",", TestConstant.VALID_ORIGIN);
        destinations = String.join(",", "22.244717,114.158532");
        distanceInMeters = TestConstant.VALID_DISTANCE;
    }

    @Test
    @DisplayName("When get distance then return distance")
    void whenGetDistanceThenReturnDistance() {
        var distanceMatrix = createDistanceMatrix(origins, destinations, DistanceMatrixElementStatus.OK, distanceInMeters);

        when(googleMapsApiGateway.getDistanceMatrix(origins, destinations)).thenReturn(distanceMatrix);

        long distanceResult = googleMapsDistanceMatrixService.getDistance(origins, destinations);

        assertEquals(distanceInMeters, distanceResult);
    }

    @Test
    @DisplayName("When get distance with empty rows then throw BizException")
    void whenGetDistanceWithEmptyRowsThenThrowBizException() {
        var distanceMatrix = createDistanceWithEmptyRows();

        when(googleMapsApiGateway.getDistanceMatrix(origins, destinations)).thenReturn(distanceMatrix);

        assertThrows(BizException.class, () -> googleMapsDistanceMatrixService.getDistance(origins, destinations));
    }

    @Test
    @DisplayName("When get distance with empty elements then throw BizException")
    void whenGetDistanceWithEmptyElementsThenThrowBizException() {
        var distanceMatrix = createDistanceWithEmptyElements();

        when(googleMapsApiGateway.getDistanceMatrix(origins, destinations)).thenReturn(distanceMatrix);

        assertThrows(BizException.class, () -> googleMapsDistanceMatrixService.getDistance(origins, destinations));
    }

    @Test
    @DisplayName("When get distance with element not OK then throw BizException")
    void whenGetDistanceWithElementNotOkThenThrowBizException() {
        var distanceMatrix = createDistanceMatrix(origins, destinations, DistanceMatrixElementStatus.NOT_FOUND, distanceInMeters);

        when(googleMapsApiGateway.getDistanceMatrix(origins, destinations)).thenReturn(distanceMatrix);

        assertThrows(BizException.class, () -> googleMapsDistanceMatrixService.getDistance(origins, destinations));
    }

    private DistanceMatrix createDistanceMatrix(String origins,
                                                String destinations,
                                                DistanceMatrixElementStatus status,
                                                long distanceInMeters) {
        DistanceMatrixElement element = new DistanceMatrixElement();
        element.status = status;
        element.distance = new Distance();
        element.distance.inMeters = distanceInMeters;

        DistanceMatrixRow row = new DistanceMatrixRow();
        row.elements = new DistanceMatrixElement[]{element};

        return new DistanceMatrix(new String[]{origins}, new String[]{destinations}, new DistanceMatrixRow[]{row});
    }

    private DistanceMatrix createDistanceWithEmptyRows() {
        return new DistanceMatrix(new String[]{}, new String[]{}, new DistanceMatrixRow[]{});
    }

    private DistanceMatrix createDistanceWithEmptyElements() {
        DistanceMatrixRow row = new DistanceMatrixRow();
        row.elements = new DistanceMatrixElement[]{};
        return new DistanceMatrix(new String[]{}, new String[]{}, new DistanceMatrixRow[]{row});
    }
}