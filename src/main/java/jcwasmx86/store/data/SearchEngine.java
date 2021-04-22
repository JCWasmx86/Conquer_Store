package jcwasmx86.store.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public record SearchEngine(StoreState state, String query) {
	public List<Predicate<AppDescriptor>> parse() {
		if (query.isEmpty()) {
			return List.of();
		}
		final var ret = new ArrayList<Predicate<AppDescriptor>>();
		final int len = this.query.length();
		int idx = 0;
		String tmp = "";
		char[] chars = this.query.toCharArray();
		while (idx < len) {
			if (Character.isWhitespace(chars[idx])) {
				//Token end
				if (!tmp.isEmpty()) {
					ret.add(this.interpretToken(tmp));
				}
				idx++;
			} else {
				tmp += chars[idx];
				idx++;
			}
		}
		return ret;
	}

	private Predicate<AppDescriptor> interpretToken(String token) {
		if (token.toCharArray()[0] == '#') {
			if (token.length() > 3) {
				final var s = token.substring(1);
				return a -> Arrays.asList(a.tags()).contains(s) || Arrays.stream(a.tags()).anyMatch(b -> b.contains(s));
			} else {
				//Tag is too short for meaningful results.
				token = token.substring(1);
			}
		}
		final String finalToken = token;
		return a -> a.description().contains(finalToken) || a.name().contains(finalToken);
	}
}
