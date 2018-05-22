package com.tnc.webrequest;

/**
 * Created by a3logics on 9/8/16.
 */
public class PremiumUserRequestBean {

    String backup_key;
    String promo_code;

    public String getBackup_key() {
        return backup_key;
    }

    public void setBackup_key(String backup_key) {
        this.backup_key = backup_key;
    }

	/**
	 * @return the promo_code
	 */
	public String getPromo_code() {
		return promo_code;
	}

	/**
	 * @param promo_code the promo_code to set
	 */
	public void setPromo_code(String promo_code) {
		this.promo_code = promo_code;
	}
}
