package pe.com.dms.inventariosoft.utils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Usuario on 03/10/2017.
 */


public interface ErrorObserver<T> extends Observer<T> {
    @Override
    default void onSubscribe(Disposable d) {
//        Timber.d("onSubscribe");
    }

//    @Override
//    default void onError(Throwable e) {
//    }

    @Override
    default void onComplete() {
//        Timber.d("onComplete");
    }

    @Override
    default void onNext(T t) {
//        Timber.d("onNext");

    }
}
