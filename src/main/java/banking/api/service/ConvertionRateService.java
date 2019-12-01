package banking.api.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConvertionRateService {
	private static final Map<String, BigDecimal> conversionRateStore = new HashMap<>();

	static {
		conversionRateStore.put("EUR-USD", new BigDecimal(1.10));
		conversionRateStore.put("USD-EUR", new BigDecimal(0.91));
		conversionRateStore.put("EUR-GBP", new BigDecimal(0.89));
		conversionRateStore.put("GBP-EUR", new BigDecimal(1.12));
		conversionRateStore.put("USD-GBP", new BigDecimal(0.81));
		conversionRateStore.put("GBP-USD", new BigDecimal(1.23));
	}

	public static BigDecimal conversionRate(final String from, final String to) {
		final BigDecimal rate = conversionRateStore.get(from + "-" + to);
		return Objects.isNull(rate) ? BigDecimal.ONE : rate;
	}

}
