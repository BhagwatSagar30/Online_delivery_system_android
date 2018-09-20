# Online_delivery_system_android

To create your own Google API Key just follow steps :

1) In AndroidManifest.xml, add the following element as a child of the <application> element, by inserting it just before the           closing </application> tag:
    <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="YOUR_API_KEY"/>
    
   Substitute your API key for YOUR_API_KEY in the value attribute. This element sets the key com.google.android.geo.API_KEY to        the value of your API key.

2) Save AndroidManifest.xml and re-build your application.

For more Details please refer the link given below:
https://developers.google.com/maps/documentation/android-sdk/signup
