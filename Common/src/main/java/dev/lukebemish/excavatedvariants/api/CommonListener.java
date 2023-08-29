/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.excavatedvariants.api;

/**
 * A listener which is fired on both the client and server; registered with the {@code excavated_variants} entrypoint or
 * through the {@link ExcavatedVariantsListener}. annotation, depending on the platform.
 */
public interface CommonListener extends Listener {
}
