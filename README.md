<h1 align="center">Guilt</h1>
<p align="center">  
Guilt is an inspiration from meta (pun intented), it tracks the apps usage and maps it with geo locoation data where the app was last used. The app is built for 
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
- Permisions
  - Location
  - UsageStat
  - Foreground Service
