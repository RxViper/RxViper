## Changelog
##### 1.0.0-RC3
* Fix `IllegalAccessException` in `NullObject` (Closes [#55]).
* Bump RxJava version to `1.3.4`.
* Plugin: rename `split` to `splitPackages`
* Small improvements

##### 1.0.0-RC2 (skipped)
* Introduce a plugin for Gradle (Closes [#50]).
* Add support for JSR-305 `Nullable`/`Nonnull` annotations (Closes [#49]).

##### 1.0.0-RC1
* Use dynamic proxying for nullsafe using view and router. (Closes [#44]).
* Added constructors for `Presenter` and `ViperPresenter`. (Closes [#41]).
* Bump RxJava version to `1.2.4`.
* Change root package `viper` -> `com.dzaitsev.rxviper`. (Closes [#39]).

##### 0.10.0
* Now we have two types of presenters: simple `Presenter` for just MVP (it manages only View) and `ViperPresenter` for VIPER that additionally manages `Router`. (Closes [#36]).
* `Mapper` implements `Func1`. (Closes [#37]).

##### 0.9.0
* Make `Presenter#getView()` and `Presenter#getRouter()` protected. (Closes [#31]).
* Change group id `rxviper` -> `com.dzaitsev.rxviper`
* Licensed files

##### 0.8.1
* update JavaDocs.

##### 0.8.0
* Removed deprecated code.

##### 0.7.0
* Updated RxJava version to 1.1.5.
* Prevent `Presenter` from taking nullable views and routers.
* Do not hold strong references to view and router in `Presenter`. (Closes [#18]).
* Added JavaDoc. (Closes [#19]).
* Ensure presenter drops the same view and router that were attached. (Closes [#22]).

##### 0.6.0
* `Interactor` works efficiently and safely with subscriptions. (Closes [#13]).
* Published on [Bintray]. 

##### 0.5.1
* `Interactor.execute(lambda, param)` respects passed param. (Closes [#9]).

##### 0.5.0
* Protect `Presenter`'s methods from overriding. (Closes [#7]).

##### 0.4.0
* Overload `Interactor#execute()` methods to allow using lambdas. (Closes [#3]).
* `Interactor` implements `rx.Subscription`. (Closes [#4]).
* Make `Interactor`'s `execute()` methods `final`. (Closes [#5]).
* Removed `viper.View` in favor of `ViewCallbacks`. (Closes [#6]).

##### 0.3.0
* `View` was deprecated to avoid confusing naming with `android.View`
* Add `ViewCallbacks` interface instead.

##### 0.2.0
* Remove `final` modifier from `Interactor#execute(subscriber, param)` (Closes [#1])
* Add `Interactor#execute(subscriber)` (Closes [#2])

##### 0.1.2
* Fixed visibility for
 * `Presenter#dropView()`,
 * `Presenter#getRouter()`,
 * `Presenter#getView()`
 
##### 0.1.1
* Submitted first usable version

[Bintray]: https://bintray.com/dmitriyzaitsev/maven/com.dzaitsev.rxviper
[#1]: https://github.com/RxViper/RxViper/issues/1
[#2]: https://github.com/RxViper/RxViper/issues/2
[#3]: https://github.com/RxViper/RxViper/issues/3
[#4]: https://github.com/RxViper/RxViper/issues/4
[#5]: https://github.com/RxViper/RxViper/issues/5
[#6]: https://github.com/RxViper/RxViper/issues/6
[#7]: https://github.com/RxViper/RxViper/issues/7
[#9]: https://github.com/RxViper/RxViper/issues/9
[#13]: https://github.com/RxViper/RxViper/issues/13
[#18]: https://github.com/RxViper/RxViper/issues/18
[#19]: https://github.com/RxViper/RxViper/issues/19
[#22]: https://github.com/RxViper/RxViper/issues/22
[#31]: https://github.com/RxViper/RxViper/issues/31
[#36]: https://github.com/RxViper/RxViper/issues/36
[#37]: https://github.com/RxViper/RxViper/issues/37
[#39]: https://github.com/RxViper/RxViper/issues/39
[#41]: https://github.com/RxViper/RxViper/issues/41
[#44]: https://github.com/RxViper/RxViper/issues/44
[#49]: https://github.com/RxViper/RxViper/issues/49
[#50]: https://github.com/RxViper/RxViper/issues/50
[#55]: https://github.com/RxViper/RxViper/issues/55