package com.star.net;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import com.star.exception.pojo.ToolException;
import com.star.lang.Assert;
import com.star.regex.Validator;
import com.star.string.HexUtil;
import com.star.string.StringUtil;

/**
 * 网络辅助工具类
 * 
 * @author http://git.oschina.net/loolly/hutool
 *
 */
public final class NetUtil {

	private NetUtil() {

	}

	/**
	 * long转ipv4
	 */
	public static String longToIpv4(final long longIP) {
		final StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(String.valueOf(longIP >>> 24)).append('.')
				.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16)).append('.')
				.append(String.valueOf((longIP & 0x0000FFFF) >>> 8)).append('.')
				.append(String.valueOf(longIP & 0x000000FF));
		return stringBuilder.toString();
	}

	/**
	 * ipv4转long
	 */
	public static long ipv4ToLong(final String strIP) {
		Assert.notBlank(strIP, "convert ip string to long failure,the input string is null");

		long result;
		if (strIP.matches(Validator.IPV4)) {
			long[] ipArray = new long[4];
			// 先找到IP地址字符串中.的位置

			final int position1 = strIP.indexOf('.');
			final int position2 = strIP.indexOf('.', position1 + 1);
			final int position3 = strIP.indexOf('.', position2 + 1);
			// 将每个.之间的字符串转换成整型

			ipArray[0] = Long.parseLong(strIP.substring(0, position1));
			ipArray[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
			ipArray[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
			ipArray[3] = Long.parseLong(strIP.substring(position3 + 1));
			result = (ipArray[0] << 24) + (ipArray[1] << 16) + (ipArray[2] << 8) + ipArray[3];
		} else {
			result = 0;
		}

		return result;
	}

	/**
	 * 验证端口是否有效
	 */
	public static boolean isValidPort(final int port) {
		return port >= 0 && port <= 0xFFFF;
	}

	/**
	 * 验证端口是否可用
	 */
	public static boolean isUsableLocalPort(final int port) {
		Assert.isTrue(isValidPort(port), "verify the port is usable failure,the port is invalid");

		boolean result;

		try {
			new Socket(longToIpv4(2130706433), port).close();
			result = false;
		} catch (IOException e) {
			result = true;
		}
		return result;
	}

	/**
	 * 是否内网地址
	 */
	public static boolean isInnerIP(final String ipAddress) {

		final long ipNum = NetUtil.ipv4ToLong(ipAddress);

		return isInner(ipNum, 167772160, 184549375) || isInner(ipNum, 2886729728L, 2887778303L)
				|| isInner(ipNum, 3232235520L, 3232301055L) || 2130706433 == ipNum;
	}

	/**
	 * 查询本机ipv4
	 */
	public static Set<String> localIpv4s() {
		Enumeration<NetworkInterface> networkInterfaces = null;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			throw new ToolException(
					StringUtil.format("Get network interface failure ,the reason is: {}", e.getMessage()), e);
		}

		Assert.notNull(networkInterfaces, "Get network interface error,the net work interfaces is null");

		final HashSet<String> ipSet = new HashSet<String>();

		while (networkInterfaces.hasMoreElements()) {
			final NetworkInterface networkInterface = networkInterfaces.nextElement();
			final Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
			while (inetAddresses.hasMoreElements()) {
				final InetAddress inetAddress = inetAddresses.nextElement();
				if (inetAddress instanceof Inet4Address) {
					ipSet.add(inetAddress.getHostAddress());
				}
			}
		}

		return ipSet;
	}

	/**
	 * ip的最后一部分用*来替代
	 */
	public static String hideIpPart(final String ipString) {
		Assert.notBlank(ipString, "hide ip's last part is failure,the input ip string is null");
		return new StringBuilder(ipString.length()).append(ipString.substring(0, ipString.lastIndexOf('.') + 1))
				.append('*').toString();
	}

	/**
	 * 查询本机mac地址
	 * 
	 * @return
	 */
	public static String getMac() {
		NetworkInterface network;
		byte[] mac;
		try {
			network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
			mac = network.getHardwareAddress();
		} catch (SocketException | UnknownHostException e) {
			throw new ToolException(StringUtil.format("get local mac address failue,the reason is: {}", e.getMessage()),
					e);
		}
		final StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			stringBuilder.append(HexUtil.byte2Hex(mac[i]));
			if (i < mac.length - 1) {
				stringBuilder.append('-');
			}
		}
		return stringBuilder.toString();
	}

	private static boolean isInner(final long userIp, final long begin, final long end) {
		return userIp >= begin && userIp <= end;
	}

}
