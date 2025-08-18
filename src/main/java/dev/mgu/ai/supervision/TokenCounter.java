package dev.mgu.ai.supervision;

public record TokenCounter(String model, int promptTokens, int generationTokens, int totalTokens) {
}
