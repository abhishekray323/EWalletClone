package com.example.User.entities.models;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class WalletCreationEvent {
        private UUID userId;
        private String phoneNumber;
}
