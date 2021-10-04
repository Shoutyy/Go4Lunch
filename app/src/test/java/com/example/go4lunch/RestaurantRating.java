package com.example.go4lunch;

import org.junit.Assert;
import org.junit.Test;


public class RestaurantRating {

    @Test
    public void restaurantRating(){

        float rating_1 = 2;
        float rating_2 = 3;
        float rating_3 = 4;
        float rating_4 = 5;

        float final_rating1 = (rating_1 / 5) * 3;
        float final_rating2 = (rating_2 / 5) * 3;
        float final_rating3 = (rating_3 / 5) * 3;
        float final_rating4 = (rating_4 / 5) * 3;


        Assert.assertEquals(1.2,final_rating1, 0.1);
        Assert.assertEquals(1.8,final_rating2, 0.1);
        Assert.assertEquals(2.4,final_rating3, 0.1);
        Assert.assertEquals(3,final_rating4, 0.1);
    }
}
