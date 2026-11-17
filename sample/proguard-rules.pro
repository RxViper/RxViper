# View and router interfaces are invoked through RxViper's dynamic proxies.
-keep interface * extends com.dzaitsev.rxviper.ViewCallbacks { *; }
-keep interface * extends com.dzaitsev.rxviper.Router { *; }
