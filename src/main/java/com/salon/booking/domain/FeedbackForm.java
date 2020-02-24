package com.salon.booking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackForm {

    @NotNull(message = "{validation.feedback_text.not_null}")
    @Length(min = 1, max = 5000, message = "{validation.feedback_text.length}")
    private String text;

    @NotNull(message = "{validation.order_id.not_null}")
    private Integer orderId;
}
