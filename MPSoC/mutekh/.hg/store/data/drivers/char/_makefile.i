         �  �      ���������Q^�P}꿧!�M^�XU�:            x���Mk�0 ��{>E;(�C�M=x�m�
}���KH��Eچ%��>�T���y~����ڛm<�O�����M Z��M^&A��(�G�����d[U_��˫gS�iS3�n�8�}-�NԀL��}�P�up�?qxo\~�W�`�*�f�}���Ɓ,a��1�q�>_�~�w�Z-��Ӯ�<,2����d��|G��!\I"�ij#�uw���Ϗ     �     O  �      }    �����z0�zC�w��:&���{t                     Cifeq ($(CONFIG_DRIVER_CHAR), defined)
 objs += device_char.o
endif
    .     e  4      �   �����ƶ?C�uE-=���q              �  �   Yifeq ($(CONFIG_DRIVER_CHAR_GAISLER_APBUART), defined)
 subdirs += gaisler-apbuart
endif

    �       �      �   ������[�@t��������vx�2                   D        �     k   �     �   �����DA��p!kx�?�.�Lܖ            x���LK-T�P�p��s�t�w	�s�w�p�r�s����QHIM��KM��R(.MJ�,*VжU(J�K���J�K�L���cL�k�?.C�R��0�/���y�990# ��>B    
     _  !     L   ��������/�ZwxVBd.oVLX�5Z               �   �   Sifeq ($(CONFIG_DRIVER_CHAR_CADENCE_UART), defined)
 subdirs += cadence_uart
endif

