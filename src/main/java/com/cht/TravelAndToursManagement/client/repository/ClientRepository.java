package com.cht.TravelAndToursManagement.client.repository;

import com.cht.TravelAndToursManagement.client.model.Client;
import java.util.List;

public interface ClientRepository {
    int count();
    
    List<Client> findByName();
    
}
