package com.cht.TravelAndToursManagement.client.service;

import com.cht.TravelAndToursManagement.client.repository.BookingRepository;
import com.cht.TravelAndToursManagement.client.repository.CustomerRepository;

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

    // Immutable class equivalent to a record
    public static class DashboardStats {
        private final int totalCustomers;
        private final int ongoingBookings;
        private final int upcomingBookings;
        private final int confirmedBookings;

        public DashboardStats(int totalCustomers, int ongoingBookings, int upcomingBookings, int confirmedBookings) {
            this.totalCustomers = totalCustomers;
            this.ongoingBookings = ongoingBookings;
            this.upcomingBookings = upcomingBookings;
            this.confirmedBookings = confirmedBookings;
        }

        public int getTotalCustomers() {
            return totalCustomers;
        }

        public int getOngoingBookings() {
            return ongoingBookings;
        }

        public int getUpcomingBookings() {
            return upcomingBookings;
        }

        public int getConfirmedBookings() {
            return confirmedBookings;
        }
    }
}
