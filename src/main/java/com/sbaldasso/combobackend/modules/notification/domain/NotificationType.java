package com.sbaldasso.combobackend.modules.notification.domain;

public enum NotificationType {
  NEW_DELIVERY_REQUEST,
  DELIVERY_ACCEPTED,
  DELIVERY_PICKED_UP,
  DELIVERY_COMPLETED,
  DELIVERY_CANCELLED,
  PAYMENT_RECEIVED,
  PAYMENT_FAILED,
  NEW_RATING,
  SYSTEM_ALERT
}