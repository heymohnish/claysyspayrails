package coop.constellation.connectorservices.claysyspayrails.handlers;

import com.xtensifi.dspco.UserData;



abstract class HandlerBase implements HandlerLogic {


    Boolean isAauthenticated(UserData userData) {

        String memberID = userData.getUserId();
        try {
            if (memberID == null || memberID == "") {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;

        }
    }

}