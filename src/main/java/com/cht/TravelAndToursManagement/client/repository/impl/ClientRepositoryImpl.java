package com.cht.TravelAndToursManagement.client.repository.impl;


import com.cht.TravelAndToursManagement.client.model.Client;
import javax.sql.DataSource;
import com.cht.TravelAndToursManagement.client.repository.ClientRepository;
import java.util.List;

public class ClientRepositoryImpl implements ClientRepository {
    private final DataSource dataSource;
    
    public ClientRepositoryImpl(DataSource dataSource) {
        // Initialize with data source if needed
        this.dataSource = dataSource;

    }
    
    

    @Override
    public int count() {
        // Dummy implementation, replace with actual database logic
        return 42; // Example static return value
    }

    @Override
    public List<Client> findByName() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
}
