## Changelog
##### 0.1.1
* Submitted first usable version

##### 0.1.2
* Fixed visibility for
 * `Presenter#dropView()`,
 * `Presenter#getRouter()`,
 * `Presenter#getView()`
 
##### 0.2.0
* Remove `final` modifier from `Interactor#execute(subscriber, param)` (Closes [#1](https://github.com/RxViper/RxViper/issues/1))
* Add `Interactor#execute(subscriber)` (Closes [#2](https://github.com/RxViper/RxViper/issues/2))

##### 0.3.0
* `View` was deprecated to avoid confusing naming with `android.View`
* Add `ViewCallbacks` interface instead.

##### 0.4.0
* Overload `Interactor#execute()` methods to allow using lambdas. (Closes [#3](https://github.com/RxViper/RxViper/issues/3)).
* `Interactor` implements `rx.Subscription`. (Closes [#4](https://github.com/RxViper/RxViper/issues/4)).
* Make `Interactor`'s `execute()` methods `final`. (Closes [#5](https://github.com/RxViper/RxViper/issues/5)).
* Removed `viper.View` in favor of `ViewCallbacks`. (Closes [#6](https://github.com/RxViper/RxViper/issues/6)).

##### 0.5.0
* Protect `Presenter`'s methods from overriding. (Closes [#7](https://github.com/RxViper/RxViper/issues/7)).

##### 0.5.1
* `Interactor.execute(lambda, param)` respects passed param. (Closes [#9](https://github.com/RxViper/RxViper/issues/9)).

##### 0.6.0
* `Interactor` works efficiently and safely with subscriptions. (Closes [#13](https://github.com/RxViper/RxViper/issues/13)).
* Published on [Bintray](https://bintray.com/rxviper/RxViper/RxViper). 

##### 0.7.0
* Updated RxJava version to 1.1.5.
* Prevent `Presenter` from taking nullable views and routers.
* Do not hold strong references to view and router in `Presenter`. (Closes [#18](https://github.com/RxViper/RxViper/issues/18)).
* Added JavaDoc. (Closes [#19](https://github.com/RxViper/RxViper/issues/19)).
* Ensure presenter drops the same view and router that were attached. (Closes [#22](https://github.com/RxViper/RxViper/issues/22)).

##### 0.8.0
* Removed deprecated code.

##### 0.8.1
* update JavaDocs.