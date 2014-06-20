Google Maps Android
===================

> My try to make look like official Google Maps app


Usage
===================
**1.Create and Register your project in Google Developers Console.**


Your google api project console must open the following API.  
- Direction API  
- Google Maps Android API v2  
- Places API 

![](apiconsole.png)

  
**2.Create *Browser* key and *Andorid* key in Credentials**

  
**3.Change your browser api key in `helpler -> SH.java`**  

    public static final String MAP_BROWSER_KEY = "<FILL BROWSER API KEY>";

**4.Change your android api key in `AndroidManifest.xml`**
       
      <meta-data
    	android:name="com.google.android.maps.v2.API_KEY"
        android:value="AIzaSyBy1aq_DXXZqxseA1A1kTofylhSqQF9fzg"/>


Preview
===================
![](preview.png)