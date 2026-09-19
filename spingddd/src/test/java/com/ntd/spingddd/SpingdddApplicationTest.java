package com.ntd.spingddd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpingdddApplicationTest {

    @Mock
    private Environment env;

    @InjectMocks
    private SpingdddApplication application;

    @Test
    void started_SetsDefaultTimezone_WhenNoTestProfile() {
        when(env.getActiveProfiles()).thenReturn(new String[]{"prod"});
        
        application.started();
        
        assertEquals("Asia/Ho_Chi_Minh", TimeZone.getDefault().getID());
    }

    @Test
    void started_SkipsTimezoneSetting_WhenTestProfileActive() {
        when(env.getActiveProfiles()).thenReturn(new String[]{"test"});
        
        TimeZone original = TimeZone.getDefault();
        try {
            application.started();
        } finally {
            TimeZone.setDefault(original);
        }
    }

    @Test
    void main_StartsApplication() {
        SpingdddApplication.main(new String[]{});
    }
}

