package com.gym.exceptions;

public class CouponDiscountExceededException extends RuntimeException {

    public CouponDiscountExceededException() {
        super();
    }

    public CouponDiscountExceededException(String message) {
        super(message);
    }

    public CouponDiscountExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouponDiscountExceededException(Throwable cause) {
        super(cause);
    }
}