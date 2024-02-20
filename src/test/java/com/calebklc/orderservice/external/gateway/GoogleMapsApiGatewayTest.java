package com.calebklc.orderservice.external.gateway;

import com.calebklc.orderservice.core.exception.BizException;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoogleMapsApiGatewayTest {

    @Mock
    private GeoApiContext geoApiContext;

    @InjectMocks
    private GoogleMapsApiGateway googleMapsApiGateway;

    private String origins;
    private String destinations;

    @BeforeEach
    void setUp() {
        origins = "40.712776,-74.005974";
        destinations = "40.712776,-74.005974";
        googleMapsApiGateway = spy(googleMapsApiGateway);
    }

    @Test
    @DisplayName("When get distance matrix then return distance matrix")
    void whenGetDistanceMatrixThenReturnDistanceMatrix() throws Exception {
        DistanceMatrix expectedDistanceMatrix = new DistanceMatrix(new String[]{origins}, new String[]{destinations}, null);

        DistanceMatrixApiRequest request = mock(DistanceMatrixApiRequest.class);

        when(googleMapsApiGateway.createDistanceMatrixApiRequest(origins, destinations)).thenReturn(request);
        when(request.await()).thenReturn(expectedDistanceMatrix);

        DistanceMatrix actualDistanceMatrix = googleMapsApiGateway.getDistanceMatrix(origins, destinations);

        assertEquals(expectedDistanceMatrix, actualDistanceMatrix);
    }

    @Test
    @DisplayName("When get distance matrix then throw api exception")
    void whenGetDistanceMatrixThenThrowApiException() throws Exception {
        DistanceMatrixApiRequest request = mock(DistanceMatrixApiRequest.class);

        when(googleMapsApiGateway.createDistanceMatrixApiRequest(origins, destinations)).thenReturn(request);
        when(request.await()).thenThrow(ApiException.class);

        assertThrows(BizException.class, () -> googleMapsApiGateway.getDistanceMatrix(origins, destinations));
    }

    @Test
    @DisplayName("When get distance matrix then throw interrupted exception")
    void whenGetDistanceMatrixThenThrowInterruptedException() throws Exception {
        DistanceMatrixApiRequest request = mock(DistanceMatrixApiRequest.class);

        when(googleMapsApiGateway.createDistanceMatrixApiRequest(origins, destinations)).thenReturn(request);
        when(request.await()).thenThrow(InterruptedException.class);

        assertThrows(BizException.class, () -> googleMapsApiGateway.getDistanceMatrix(origins, destinations));
    }

    @Test
    @DisplayName("When get distance matrix then throw io exception")
    void whenGetDistanceMatrixThenThrowIOException() throws Exception {
        DistanceMatrixApiRequest request = mock(DistanceMatrixApiRequest.class);

        when(googleMapsApiGateway.createDistanceMatrixApiRequest(origins, destinations)).thenReturn(request);
        when(request.await()).thenThrow(IOException.class);

        assertThrows(BizException.class, () -> googleMapsApiGateway.getDistanceMatrix(origins, destinations));
    }
}