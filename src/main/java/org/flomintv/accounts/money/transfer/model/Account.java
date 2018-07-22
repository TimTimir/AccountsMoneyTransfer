package org.flomintv.accounts.money.transfer.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Objects;

@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class Account {

    @XmlAttribute
    private Integer userId;
    @XmlAttribute
    private BigDecimal currentAmount;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(userId, account.userId) &&
                Objects.equals(currentAmount, account.currentAmount);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, currentAmount);
    }

    @Override
    public String toString() {
        return "Account{" +
                "userId=" + userId +
                ", currentAmount=" + currentAmount +
                '}';
    }
}
