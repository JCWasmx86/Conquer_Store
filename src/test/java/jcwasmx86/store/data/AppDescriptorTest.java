package jcwasmx86.store.data;

import jcwasmx86.store.data.AppDescriptor.Hashes;
import org.junit.Test;

public class AppDescriptorTest {

	@Test
	public void checkPassingOfHashValidation() {
		final var bytes = "foo\n".getBytes();
		final var h = new Hashes("d3b07384d113edec49eaa6238ad5ff00", "f1d2d2f924e986ac86fdf7b36c94bcdf32beec15",
			"b5bb9d8014a0f9b1d61e21e796d78dccdf1352f23cd32812f4850b878ae4944c");
		h.validate(bytes, "test");
	}

	@Test(expected = SecurityException.class)
	public void failHashValidation() {
		final var bytes = "foo\n".getBytes();
		final var h = new Hashes("d3b07384d113edec49eaa6238ad5ff00", "f1d2d2f924e986ac86fdf7b36c94bcdf32beec15",
			"a5bb9d8014a0f9b1d61e21e796d78dccdf1352f23cd32812f4850b878ae4944c");
		h.validate(bytes, "test");
	}
}