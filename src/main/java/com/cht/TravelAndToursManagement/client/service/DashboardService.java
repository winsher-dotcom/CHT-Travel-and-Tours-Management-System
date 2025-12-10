package com.cht.TravelAndToursManagement.client.service;

public class DashboardService {
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;

    public DashboardService(BookingRepository bookingRepository, CustomerRepository customerRepository) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
    }

    public DashboardStats getDashboardStats() {
        return new DashboardStats(
                customerRepository.count(),
                bookingRepository.countByStatus("ongoing"),
                bookingRepository.countByStatus("upcoming"),
                bookingRepository.countByStatus("confirmed")
        );
    }

    public record DashboardStats(
            int totalCustomers,
            int ongoingBookings,
            int upcomingBookings,
            int confirmedBookings
    ) {
    }
}
