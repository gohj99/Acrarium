package com.faendir.acra.ui.view.app.tabs;

import com.faendir.acra.model.App;
import com.faendir.acra.service.DataService;
import com.faendir.acra.ui.base.ActiveChildAware;
import com.faendir.acra.ui.base.SecurityAwareHasUrlParameter;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.router.BeforeEvent;

import java.util.Optional;

/**
 * @author lukas
 * @since 14.07.18
 */
public abstract class AppTab<T extends Component> extends Composite<T> implements SecurityAwareHasUrlParameter {
    private final DataService dataService;
    private App app;

    public AppTab(DataService dataService) {
        this.dataService = dataService;
    }

    public DataService getDataService() {
        return dataService;
    }

    @Override
    public void setParameterSecure(BeforeEvent event, Integer parameter) {
        Optional<App> app = dataService.findApp(parameter);
        if (app.isPresent()) {
            this.app = app.get();
            init(this.app);
        } else {
            event.rerouteToError(IllegalArgumentException.class);
        }
    }

    abstract void init(App app);

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        Optional<Component> parent = getParent();
        while (parent.isPresent()) {
            if (parent.get() instanceof ActiveChildAware) {
                //noinspection unchecked
                ((ActiveChildAware<AppTab<?>, App>) parent.get()).setActiveChild(this, app);
                break;
            }
            parent = parent.get().getParent();
        }
    }
}
