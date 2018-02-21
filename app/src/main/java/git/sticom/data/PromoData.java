package git.sticom.data;

/**
 * Created by Ade on 08/02/2018.
 */

public class PromoData {
    String promo_id, promo_judul;

    public PromoData() {
    }

    public PromoData(String promo_id, String promo_judul) {
        this.promo_id = promo_id;
        this.promo_judul = promo_judul;
    }

    public String getPromo_id() {
        return promo_id;
    }

    public void setPromo_id(String promo_id) {
        this.promo_id = promo_id;
    }

    public String getPromo_judul() {
        return promo_judul;
    }

    public void setPromo_judul(String promo_judul) {
        this.promo_judul = promo_judul;
    }
}
