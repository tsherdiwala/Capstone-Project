# StackyAndroid
StackyAndroid helps to track questions on stackoverflow site to get periodic updates.

### Changes to be done to before running

####res/strings.xml
 Change the line below to include banner ad unit from Admob
```
  <string name="banner_ad_unit_id">AD_UNIT_ID_HERE</string>
```

####java/com/knoxpo/stackyandroid/utils/Contants.java
Change the ad app id and test device id here

```
public static class Ads{
        public static final String
                AD_APP_ID = "ADD_APP_ID_HERE",
                TEST_DEVICE_ID = "TEST_DEVICE_ID_HERE";
    }
```

####goolge-services.json
The file needs to be download and kept into the following path so that it could be picked up by Google Plugin
```
app/google-services.json
```
