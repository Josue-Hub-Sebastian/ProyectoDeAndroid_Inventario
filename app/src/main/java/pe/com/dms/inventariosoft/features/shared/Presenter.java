package pe.com.dms.inventariosoft.features.shared;

/**
 * Every presenter in the app must either implement this interface or extend BasePresenter
 * indicating the BaseContractView type that wants to be attached with.
 */
public interface Presenter<V extends BaseContractView> {

    void attachView(V mvpView);

    void detachView();
}
