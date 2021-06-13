# EnocSampleApp
## Application Highlights


This is sample application in Kotlin for login authentication and displaying user information on sucessfull login. Application supports from API 21 to 30.
Application will work in offline and online (actual communication with server)  depending on configuration. In this sample by default offline mode is enabled.



## Features
- User can login and check his/her profile
- on successfull login User credentials are stored in secure way using Android keystore
- For Subsequent auto login , stored credenticals can be used
- User can take capture image from camera or choose from Gallery for profile picture
- Selected Image will be uploaded on server as well cached locally
- Image is compressed before uploading it to server
- Uploaded image can be loaded again using url
- Email Id is used to fetch profile picture from Gravatar


## Tech
EnocSampleApp is developed using Kotlin and intended to use latest available component
Application uses following Plugins, libraries or open source projects and latest Android components to work properly:

- [Glide] - For Image downloading and resizing!
- [Retrofit] - For Network request managment
- [Dagger Hilt] - To remove boilerplate code and apply data binding.
- [NavController] - Navigation between UI components
- [Architecture] - MVVM is used for clean code
- [Gravatar] - Gravatar url is used fro fetching globally available profile picture
- [ShapeableImageView] - Google ShapeableImageView is used to make profile picture fit in circle
- [ConstraintLayout] - Android widget ConstraintLayout is used for UI design 
- [gson]- for reponse parsing


## UpComing

You can see a future update of sample with Redux architecture and Room database for offline usage

#### Building for source

For Offline usage:
 build --debug
 
 
For Online Usage
 build --release
 Also need to configure base Url for online usage


#### Purpose 
> Learning


## License

**Free**





