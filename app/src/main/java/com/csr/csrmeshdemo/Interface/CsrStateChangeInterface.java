package com.csr.csrmeshdemo.Interface;

/**
 * Created by yaohj on 2017/3/2.
 */

public class CsrStateChangeInterface {
    public interface ConnectionStateChange{
      public void OnConnected(boolean already_network_setting);
    }

    public interface SecurityCallback{
        public void Security(boolean already_security);
    }
}
