package org.flomintv.accounts.money.transfer.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Objects;

@XmlRootElement(name = "transfer")
@XmlAccessorType(XmlAccessType.FIELD)
public class Transfer {

    @XmlAttribute
    private Integer userIdFrom;
    @XmlAttribute
    private Integer userIdTo;
    @XmlAttribute
    private BigDecimal amount;
    @XmlAttribute
    private String comment;

    public Integer getUserIdFrom() {
        return userIdFrom;
    }

    public void setUserIdFrom(Integer userIdFrom) {
        this.userIdFrom = userIdFrom;
    }

    public Integer getUserIdTo() {
        return userIdTo;
    }

    public void setUserIdTo(Integer userIdTo) {
        this.userIdTo = userIdTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return Objects.equals(userIdFrom, transfer.userIdFrom) &&
                Objects.equals(userIdTo, transfer.userIdTo) &&
                Objects.equals(amount, transfer.amount) &&
                Objects.equals(comment, transfer.comment);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userIdFrom, userIdTo, amount, comment);
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "userIdFrom=" + userIdFrom +
                ", userIdTo=" + userIdTo +
                ", amount=" + amount +
                ", comment='" + comment + '\'' +
                '}';
    }
}
