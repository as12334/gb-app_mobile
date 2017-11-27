package so.wwb.gamebox.mobile.init;

import so.wwb.gamebox.web.init.CommonCtxLoaderListener;

/**
 * Created by Kevice on 2015/3/23 0023.
 */
public class CtxLoaderListener extends CommonCtxLoaderListener {
    @Override
    public void customInit() throws Exception {
        super.customInit();
        PassportContextPath="";
    }
}
