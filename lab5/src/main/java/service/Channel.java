package service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Channel {
    protected PropertyChangeSupport propertyChangeSupport;
    private List<String> data;

    public Channel() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        data = new CopyOnWriteArrayList<>();
    }

    public void setData(List<String> data) {
        List<String> oldData = this.data;
        this.data = data;
        propertyChangeSupport.firePropertyChange("ChannelChanged", oldData, data);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void clearData() {
        data.clear();
    }

    public List<String> getData() {
        return new CopyOnWriteArrayList<>(data);
    }
}
