         J   I      6�����������[��㗬�y���(            u
ifeq ($(CONFIG_DRIVER_CHAR_PL011), defined)
subdirs += pl011uart
endif

     J     U   �      O    �������U��d�w�˓mWV���               I   I   Iifeq ($(CONFIG_DRIVER_ARM_A9MPCORE), defined)
subdirs += a9mpcore
endif

     �     R   �      U   �����KA1���H]ed�	�̪�               I   I   Fifeq ($(CONFIG_DRIVER_ICU_PL390), defined)
subdirs += pl390icu
endif

