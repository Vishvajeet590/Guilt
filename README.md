<h1 align="center">Guilt</h1>
<p align="center">  
Guilt is an inspiration from Meta (pun intented), it tracks the apps usage and maps it with geo locoation data where the app was last used. The app is built to collect data points for users app usage behaviour at different locations.
</p>

<p align="center">
<img src="https://user-images.githubusercontent.com/42716731/185765736-3baeb29a-efa3-43a6-8410-9a509503f313.png")
" />
</p>

## Tech stack & Open-source libraries
- Minimum SDK level 21
- 100% [Kotlin](https://kotlinlang.org/) based + [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) for asynchronous.
- JetPack
  - Lifecycle - dispose observing data when lifecycle state changes.
  - ViewModel - UI related data holder, lifecycle aware.
  - Room DB
- Permisions
  - Location
  - UsageStat
  - Foreground Service
  
  
## Basic overview
- As the app starts it starts a foreground service. The service calls the usageStat api to fetch recent events, and feteches the location at that moment. 
- Then service inserts that data in sql db (ROOM).
- When the app is opened again, it fetches the latest entries from DB and shows it on the home.
